# Generated by Django 3.2.6 on 2021-09-05 14:19

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('content', '0008_alter_content_file'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='content',
            name='file',
        ),
        migrations.RemoveField(
            model_name='content',
            name='image',
        ),
        migrations.RemoveField(
            model_name='content',
            name='is_preview',
        ),
        migrations.AddField(
            model_name='content',
            name='content',
            field=models.FileField(null=True, upload_to=''),
        ),
        migrations.AddField(
            model_name='content',
            name='preview',
            field=models.FileField(null=True, upload_to=''),
        ),
        migrations.AlterField(
            model_name='content',
            name='tags',
            field=models.ManyToManyField(to='content.Tags'),
        ),
    ]
