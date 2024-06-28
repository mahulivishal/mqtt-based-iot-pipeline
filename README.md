# mqtt-based-iot-pipeline
This PoC aims at detailing out an example to build IoT pipelines using MQTT, Flink and Kafka through a specific usecase.


# Protobuf Compilation
protoc -I=$SRC_DIR --python_out=$DST_DIR $SRC_DIR/device-data.proto
protoc -I. --python_out=iot/proto/proto_py device_data.proto

# Creating a Virtual Env in Python
brew install virtualenv
python3 -m venv ~/pyvenv
source ~/pyvenv/bin/activate
pip3 install -r requirements.txt
