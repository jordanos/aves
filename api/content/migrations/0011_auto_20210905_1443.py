# Generated by Django 3.2.6 on 2021-09-05 14:43

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('content', '0010_auto_20210905_1423'),
    ]

    operations = [
        migrations.AlterField(
            model_name='content',
            name='content',
            field=models.FileField(blank=True, null=True, upload_to=''),
        ),
        migrations.AlterField(
            model_name='content',
            name='image',
            field=models.ImageField(blank=True, null=True, upload_to=''),
        ),
        migrations.AlterField(
            model_name='content',
            name='preview',
            field=models.FileField(blank=True, null=True, upload_to=''),
        ),
        migrations.AlterField(
            model_name='content',
            name='upload',
            field=models.FileField(blank=True, null=True, upload_to=''),
        ),
    ]