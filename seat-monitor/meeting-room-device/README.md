### meeting-room-device

This module is the AndroidThings application that will run on an IMX-P7D

Each meeting room should have one AndroidThings device that will support up to 16 individual seats.
This is due to the fact we are using the ADS1015 ADC running on I2C, we can configure the ADC to have 1 of 4 different addresses, each ADC can read 4 sensors, giving us 4x4=16 sensor readings.

When the meeting room is first setup the weight sensors all need to be positioned below the cushions and then configured.
There is a configuration button on the AT board, and this resets all sensor data so that the cushions do not give false readings.

Once running, JSON data is sent every 5 seconds via MQTT up to Google Cloud IoT core. The format is as follows:

```json
{
   "location" : "london-fishbowl",
   "totalLoadGauges" : 9,
   "dataLoadGauges" : [
   {
     "id" : "ldn1",
     "weight" : 1234
   },
   {
     "id" : "ldn2",
     "weight" : 0
   },
       ...etc
   ]

}
```

Note that the "weight" in this JSON is not a calculated value of KG or other weight. It is a relative value from the rest weight of 0.
The data collection is anonymous and indistinguishable by design.
