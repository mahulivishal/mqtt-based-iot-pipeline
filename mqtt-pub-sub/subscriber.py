import json

from paho.mqtt import client as mqtt_client
from iot.proto import device_data_pb2

broker = '127.0.0.1'
port = 1883
topic = "vishal/poc/mqtt/bms/"
device = "d_01"
sub_client = f"sub_{device}"
username = 'vishal'
password = 'vishal@123'


def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("SUBSCRIBER | Connected to MQTT Broker!")
        else:
            print("SUBSCRIBER | Failed to connect, return code %d\n", rc)
    print(f'SUBSCRIBER | Connecting to the MQTT Broker - broker: {broker}, port: {port}, client_id: {sub_client}')
    client = mqtt_client.Client(sub_client)
    #client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.reconnect_delay_set(1, 10)
    client.connect(broker, port)
    return client


def subscribe(client):
    device_topic = topic + device

    def on_message(client, userdata, msg):
        try:
            print(f"SUBSCRIBER | Received data at `{msg.topic}` topic")
            device_data = device_data_pb2.DeviceBMSData()
            device_data.ParseFromString(msg.payload)
            device_data_json = {
                "id": device_data.deviceId,
                "soc": device_data.soc,
                "timestamp": device_data.timestamp
            }
            print(f"SUBSCRIBER | Received `{json.dumps(device_data_json)}`")
        except Exception as e:
            print("SUBSCRIBER | Error while parsing data!")
            print(e)

    client.subscribe(device_topic, qos=1)
    print(f'SUBSCRIBER | Subscribing to topic - {device_topic}')
    client.on_message = on_message


def run():
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()


if __name__ == '__main__':
    run()
