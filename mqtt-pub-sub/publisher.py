import random
import time

from paho.mqtt import client as mqtt_client
from iot.proto import device_data_pb2

broker = '127.0.0.1'
port = 1883
topic = "vishal/poc/mqtt/bms/"
device = "d_01"
pub_client = f"pub_{device}"
soc_min = 5
soc_max = 80
no_of_records = 100
username = 'vishal'
password = 'vishal@123'


def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("PUBLISHER | Connected to MQTT Broker!")
        else:
            print("PUBLISHER | Failed to connect, return code %d\n", rc)

    print(f'PUBLISHER | Connecting to the MQTT Broker - broker: {broker}, port: {port}, client_id: {pub_client}')
    client = mqtt_client.Client(pub_client)
    # client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def publish(client):
    msg_count = 1
    device_topic = topic + device
    while True:
        time.sleep(2)
        device_id, protobuf = generate_device_data_proto()
        msg = protobuf.SerializeToString()
        result = client.publish(device_topic, msg, qos=1)
        status = result[0]
        if status == 0:
            print(f"PUBLISHER | Sent `{msg}` to topic `{device_topic}`")
        else:
            print(f"PUBLISHER | Failed to send message to topic {device_topic}")
        msg_count += 1
        if msg_count > no_of_records:
            break


def generate_device_data_proto():
    device_data = device_data_pb2.DeviceBMSData()
    device_id = device
    device_data.deviceId = device_id
    device_data.soc = random.randint(soc_min, soc_max)
    device_data.timestamp = int(time.time() * 1000)
    return device_id, device_data


def run():
    client = connect_mqtt()
    client.loop_start()
    publish(client)
    client.loop_stop()


if __name__ == '__main__':
    run()
