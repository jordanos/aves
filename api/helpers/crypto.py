import uuid
from Crypto.Util import Counter
from Crypto.Cipher import AES
import os
import sys
import time


class CustomAES:
    MODE_ENCRYPT = 1
    MODE_DECRYPT = 2
    BUFFER = 1024

    def __init__(self):
        pass

    @staticmethod
    def get_counter(nonce):
        return Counter.new(128 - (len(nonce) * 8), prefix=nonce, initial_value=0)

    @staticmethod
    def do_cipher(cipher, mode, in_file, out_file, callback, uid: uuid.UUID):
        try:
            # Remove file if it existes because we are appending to it
            if os.path.exists(out_file):
                os.remove(out_file)
            size = os.path.getsize(in_file)

            callback("Opening streams...")
            input_stream = open(in_file, "rb")
            ouput_stream = open(out_file, "ab")

            # read bytes length of 16 from in_file, cipher them and append cipher bytes to out_file
            c = 0
            ouput_stream.write(uid.bytes)
            while True:
                bytes = input_stream.read(CustomAES.BUFFER)
                if bytes == b"":
                    break
                if mode == CustomAES.MODE_ENCRYPT:
                    ouput_stream.write(cipher.encrypt(bytes))
                elif mode == CustomAES.MODE_DECRYPT:
                    ouput_stream.write(cipher.decrypt(bytes))
                callback(message="Ciphering", val=c, end=size, t="progress")
                c += CustomAES.BUFFER
            return True
        finally:
            callback("Closing streams...", t="text")
            input_stream.close()
            ouput_stream.close()

    @staticmethod
    def get_cipher(key, nonce) -> AES.AESCipher:
        ctr = CustomAES.get_counter(nonce)
        cipher = AES.new(key, AES.MODE_CTR, counter=ctr)
        return cipher

    @staticmethod
    def console_log(message, val=0, end=0, t=""):
        if t == "progress":
            CustomAES.progressBar(message, value=val, endvalue=end)
            return
        sys.stdout.write(f"{message}\n")
        sys.stdout.flush()

    @staticmethod
    def progressBar(message, value, endvalue, bar_length=20):
        percent = float(value) / endvalue
        arrow = "-" * int(round(percent * bar_length) - 1) + ">"
        spaces = " " * (bar_length - len(arrow))

        sys.stdout.write(
            "\r{0}: [{1}] {2}%".format(
                message, arrow + spaces, int(round(percent * 100))
            )
        )
        sys.stdout.flush()


# key = b'Nbm@"1^+z*B\Zgd8'
# nonce = 'IhbA.wCe&=}-'
# in_file = '/home/jordan/Documents/Projects/final_project/Aves-server/Aves-sever/aves/media/contents/content/06bf5b6e-c35e-4330-8cbc-44703cdf5d4b.aves'
# out_file = '/home/jordan/Documents/Projects/final_project/Aves-server/Aves-sever/aves/media/contents/content/sept9-test2.mp4'

# cipher = CustomAES.get_cipher(key, nonce)
# CustomAES.do_cipher(cipher, CustomAES.MODE_DECRYPT,
#                     in_file, out_file, CustomAES.console_log)
