from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Optional as _Optional

DESCRIPTOR: _descriptor.FileDescriptor

class DeviceBMSData(_message.Message):
    __slots__ = ("deviceId", "soc", "timestamp")
    DEVICEID_FIELD_NUMBER: _ClassVar[int]
    SOC_FIELD_NUMBER: _ClassVar[int]
    TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
    deviceId: str
    soc: float
    timestamp: int
    def __init__(self, deviceId: _Optional[str] = ..., soc: _Optional[float] = ..., timestamp: _Optional[int] = ...) -> None: ...
