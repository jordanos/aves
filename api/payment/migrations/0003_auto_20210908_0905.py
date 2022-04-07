# Generated by Django 3.2.6 on 2021-09-08 09:05

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('payment', '0002_deposit_purchase_withdraw'),
    ]

    operations = [
        migrations.AlterField(
            model_name='purchase',
            name='payment_method',
            field=models.CharField(choices=[('Yenepay', 'Yenepay')], max_length=50),
        ),
        migrations.AlterField(
            model_name='withdraw',
            name='payment_method',
            field=models.CharField(choices=[('Yenepay', 'Yenepay')], max_length=50),
        ),
        migrations.DeleteModel(
            name='Deposit',
        ),
        migrations.DeleteModel(
            name='Method',
        ),
    ]
