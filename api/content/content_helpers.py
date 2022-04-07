from uuid import UUID
from django.core.files.uploadedfile import UploadedFile
from .models import Content
from helpers.utils import Util
from helpers.crypto import CustomAES
import secrets
import string
import threading


class ContentThread(threading.Thread):
    def __init__(self, uid: UUID, upload_loc: str) -> None:
        self.uid = uid
        self.upload_loc = upload_loc
        # self.image_loc = image
        return threading.Thread.__init__(self)

    def run(self):
        # save image
        # save file to upload
        # encrypt file and save to content
        # make preview and save to preview
        # save changes to database(upload location, content location, preview location)

        base = Util.get_media_base()

        content_loc = f"/contents/content/{str(self.uid)}.aves"
        preview_loc = f"/contents/preview/{str(self.uid)}.mp4"

        key = "".join(
            secrets.choice(string.ascii_letters + string.digits + string.punctuation)
            for x in range(16)
        )
        nonce = "".join(
            secrets.choice(string.ascii_letters + string.digits + string.punctuation)
            for x in range(8)
        )

        cipher = CustomAES.get_cipher(key, nonce)
        is_encrypted = CustomAES.do_cipher(
            cipher,
            CustomAES.MODE_ENCRYPT,
            base + self.upload_loc,
            base + content_loc,
            CustomAES.console_log,
            self.uid,
        )
        if not is_encrypted:
            print("==============ERROR")
            return
        content = Content.objects.get(id=self.uid)

        # save encrypted content
        content = Content.objects.get(id=self.uid)
        content.key = key
        content.nonce = nonce
        content.content = content_loc

        # generate a preview for the uploaded content
        Util.get_preview(base + self.upload_loc, base + preview_loc)
        content.preview = preview_loc
        # save the content data in database

        content.is_finished = True
        content.save()
        print("==============DONE")
