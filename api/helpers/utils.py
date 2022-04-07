from django.core.mail import EmailMessage
import uuid
from moviepy.editor import VideoFileClip, concatenate_videoclips
from aves.settings import BASE_DIR
import os

# from django.core.files.uploadhandler import FileUploadHandler
# from .crypto import CustomAES
import threading


class EmailThread(threading.Thread):
    def __init__(self, email) -> None:
        self.email = email
        threading.Thread.__init__(self)

    def run(self) -> None:
        self.email.send(fail_silently=True)


# class UploadHandler(FileUploadHandler):
#     def receive_tedata_chunk(self, raw_data: bytes, start: int):
#         cipher = CustomAES.get_cipher(b'1234123412341234', 'abcd')
#         raw_data = cipher.encrypt(raw_data)
#         return super().receive_data_chunk(raw_data, start)


class Util:
    """
    Utils class for the project
    """

    SUB_CLIP_LENGTH = 2
    SUB_CLIP_AMOUNT = 2

    def __init__(self) -> None:
        pass

    @staticmethod
    def get_file_path(instance, filename):
        return "/".join(
            ["content", "images", f"{str(instance.pk)}.{Util.get_filetype(filename)}"]
        )

    @staticmethod
    def send_email(data: dict):
        """
        Send email using params from data dictionary

        :param data: a dict object contaning subjext, body, and to -> recipient

        :return: True if send success
        """
        email = EmailMessage(subject=data["subject"], body=data["body"], to=data["to"])
        email_thread = EmailThread(email)
        email_thread.start()
        return True

    @staticmethod
    def create_uuid() -> uuid.UUID:
        return uuid.uuid4()

    @staticmethod
    def get_uuid_from_byte(bytes) -> uuid.UUID:
        return uuid.UUID(bytes=bytes)

    @staticmethod
    def get_filetype(filename: str) -> str:
        # returns a file type from a filename
        filetype = filename.split(".")
        if len(filetype) >= 2:
            return filetype[len(filetype) - 1]
        return ""

    @staticmethod
    def get_subclip_offsets(duration: int) -> list:
        # Initializer offset list to hold offsets
        offsets = []
        # Get how many parts can we get from the total clip duration
        parts = int(duration / Util.SUB_CLIP_LENGTH)
        # Get equal parts by dividing total parts with amount of parts we need
        divs = int(parts / Util.SUB_CLIP_AMOUNT)
        # Append offsets by increamenting div times each time
        for i in range(0, parts, divs):
            offset = i * Util.SUB_CLIP_LENGTH
            offsets.append(offset)
        return offsets

    @staticmethod
    def get_preview(in_file: str, out_file: str) -> bool:
        """
        Gets a small preview video from a video file

        :param in_file: input video file path
        :param out_file: output video file path

        :return: True if success
        """
        clip = VideoFileClip(in_file)
        if clip.duration <= ((Util.SUB_CLIP_AMOUNT + 1) * Util.SUB_CLIP_LENGTH) + 4:
            return False
        subclip_offsets = Util.get_subclip_offsets(clip.duration)
        subclips = [
            clip.subclip(offset, offset + Util.SUB_CLIP_LENGTH)
            for offset in subclip_offsets
        ]
        final_clip = concatenate_videoclips(subclips)
        final_clip.write_videofile(out_file)
        return True

    @staticmethod
    def get_media_base() -> str:
        path = os.path.join(BASE_DIR, "media")
        return str(path)
