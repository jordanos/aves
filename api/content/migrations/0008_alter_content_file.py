# Generated by Django 3.2.6 on 2021-09-05 11:23

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('content', '0007_rename_link_content_file'),
    ]

    operations = [
        migrations.AlterField(
            model_name='content',
            name='file',
            field=models.FileField(blank=True, upload_to='<django.db.models.fields.CharField>'),
        ),
    ]
