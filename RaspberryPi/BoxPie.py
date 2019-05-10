'''
The Script connects over Mqtt phao to the Brocker and handles the mqtt
messages. In addition a thread with the Box Controller (which controlls 
LCD,Keypad,Opening,Closing,etc.) is created. 
'''

from threading import Thread
import time
import os
import paho.mqtt.client as mqtt
import json
import RPi.GPIO as GPIO

#ip = "169.254.142.164" #LAN Laptop
ip = "192.168.178.49"   #WLAN 
client_name = "38678_2_rec"
area = "/38678/2"


def startprgm(i):
	print "Running thread %d" % i
	if (i==0):
		time.sleep(1)
		print("Running Box_Contoller.py")
		os.system("sudo python /home/pi/Desktop/Box_Python_Code/BoxControll.py")
	else:
		pass


# Boxcluster subscribes to all necessary Topics
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

    client.subscribe("newCode"+area)
    client.subscribe("rentBox"+area)
    client.subscribe("boxKey" + area)

#Sorts data by received Topics
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))
    topic = msg.topic.encode("ascii")
    topic = topic.split("/",1)
    sendData = json.loads(msg.payload)
    #Topic : rentBox (User rents one new Box)
    if(topic[0] == 'rentBox'):
	print("rent topic")
	boxData = sendData
	print("boxData", boxData)
	with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data","r") as fin:
	    data = json.load(fin)
	if data:
	    check = True
	    for i in range(len(data)):
		if(data[i].get("ID") == boxData.get("ID")):
		    print("test")
		    data[i] = boxData
		    check = False
		    break
	    if check:
		data.append(boxData)
		
	    with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data", "w") as fout:
		    json.dump(data,fout)
		    print("successfully updated box data(rent)")
		    
    #Topic:newCode (User changes all his Private Keys)
    elif(topic[0] == 'newCode'):
	boxData = sendData.get("Box")
	with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data","r") as fin:
	    data = json.load(fin)
	if data:
	    for j in range(len(boxData)):
		check = True
		for i in range(len(data)):
		    if(data[i].get("ID") == boxData[j].get("ID")):
			data[i] = boxData[j]
			check = False
			break
		if check:
		    data.append(sendData)
	    with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data", "w") as fout:
		    json.dump(data,fout)
		    print("successfully updated box data (new code)")
    
    #Topic : rentBox (User rents one new Box)
    elif(topic[0] == 'boxKey'):
	boxData = sendData
	with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data","r") as fin:
	    data = json.load(fin)
	if data:
	    for i in range(len(data)):
		if(data[i].get("ID") == boxData.get("ID")):
		    print(data[i])
		    print(data[i]["BoxPIN"])
		    print(boxData["BoxPIN"])
		    data[i]["BoxPIN"] = boxData["BoxPIN"]
		    break
	    with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data", "w") as fout:
		    json.dump(data,fout)
		    print("successfully updated boxKey for Box with id:" , boxData.get("ID"))
    return

#Initialize client
client = mqtt.Client(client_name)
client.on_connect = on_connect
client.on_message = on_message

client.connect(ip, 1883, 60)

#Start the Box Controller Programm in a new thread
t= Thread(target=startprgm, args=(0,))
t.setDaemon(True)
t.start()

try:
    client.loop_forever()
    
except KeyboardInterrupt:  # When 'Ctrl+C' is pressed, the child program destroy() will be  executed.
    client.disconnect()
        

	
