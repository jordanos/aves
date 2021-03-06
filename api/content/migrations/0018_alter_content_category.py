# Generated by Django 3.2.6 on 2021-09-09 04:21

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("content", "0017_content_length"),
    ]

    operations = [
        migrations.AlterField(
            model_name="content",
            name="category",
            field=models.CharField(
                choices=[
                    ("Pyschology", "Psychology"),
                    ("Tutorial", "Tutorial"),
                    ("Movie", "Movie"),
                    ("Music", "Music"),
                    ("Audio Book", "Audio Book"),
                ],
                max_length=25,
            ),
        ),
    ]
