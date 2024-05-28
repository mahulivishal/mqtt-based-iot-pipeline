import random
import time

from paho.mqtt import client as mqtt_client
from proto_py import device_data_pb2

broker = 'localhost'
port = 18083
topic = "vishal/poc/mqtt/bms/"
device = client_id = "d_01"
soc_min = 5
soc_max = 80
no_of_records = 100


# username = 'emqx'
# password = 'public'

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    client = mqtt_client.Client(client_id)
    # client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def publish(client):
    msg_count = 1
    while True:
        time.sleep(1)
        device_id, protobuf = generate_device_data_proto()
        print(protobuf)
        msg = protobuf.SerializeToString()
        result = client.publish(topic + device_id, msg)
        status = result[0]
        if status == 0:
            print(f"Send `{msg}` to topic `{topic}`")
        else:
            print(f"Failed to send message to topic {topic}")
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
