# mqtt-based-iot-pipeline
This PoC aims at detailing out an example to build IoT pipelines using MQTT, Flink and Kafka through a sepcific usecase.


# Protobuf Compilation
protoc -I=$SRC_DIR --python_out=$DST_DIR $SRC_DIR/device-data.proto
protoc -I=mqtt-pub-sub --python_out=mqtt-pub-sub/proto_py --pyi_out=mqtt-pub-sub/proto_py mqtt-pub-sub/device_data.proto