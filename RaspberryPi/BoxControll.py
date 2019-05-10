import sys
import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt
import time
from datetime import datetime, date
sys.path.append("/home/pi/Desktop/Box_Python_Code/Imports")
import Keypad
import json
from PCF8574 import PCF8574_GPIO
from Adafruit_LCD1602 import Adafruit_CharLCD

#MQTT sender
ip = "192.168.178.49"   #WLAN 
client_name = "38678_2_sen"
area = "/38678/2"



ledRedPin = [33,29,38]   #GPIO 13 Pin
ledGreenPin = [35,31,40] #GPIO 19 Pin 
ROWS = 4        # number of rows of the Keypad
COLS = 4        #number of columns of the Keypad
keys =  [   '1','2','3','A',    #key code
            '4','5','6','B',
            '7','8','9','C',
            '*','0','#','D'     ]
rowsPins = [12,16,18,22]        #connect to the row pinouts of the keypad
colsPins = [19,15,13,11]        #connect to the column pinouts of the keypad
#initializes the LCD display
PCF8574_address = 0x27  # I2C address of the PCF8574 chip.
PCF8574A_address = 0x3F  # I2C address of the PCF8574A chip.
# Create PCF8574 GPIO adapter.
try:
    mcp = PCF8574_GPIO(PCF8574_address)
except:
    try:
        mcp = PCF8574_GPIO(PCF8574A_address)
    except:
        print ('I2C Address Error !')
        exit(1)
# Create LCD, passing in MCP GPIO adapter.
lcd = Adafruit_CharLCD(pin_rs=0, pin_e=2, pins_db=[4,5,6,7], GPIO=mcp)
#keypad init
keypad = Keypad.Keypad(keys,rowsPins,colsPins,ROWS,COLS)    #create Keypad object
keypad.setDebounceTime(50)      #set the debounce time

    
mcp.output(3,1)     # turn on LCD backlight
lcd.begin(16,2)     # set number of LCD lines and columns


# setups the both LED
def setupLED():
    GPIO.setmode(GPIO.BOARD)       # Numbers GPIOs by physical location
    for i in range(len(ledRedPin)):
        GPIO.setup(ledRedPin[i], GPIO.OUT)   # Set ledRedPin's mode is output
        GPIO.output(ledRedPin[i], GPIO.HIGH) # Set ledRedPin high to on led
    for i in range(len(ledGreenPin)):
        GPIO.setup(ledGreenPin[i], GPIO.OUT)   # Set ledGreenPin's mode is output
        GPIO.output(ledGreenPin[i], GPIO.LOW) # Set ledGreenPin low to off led
    print ('using pin %(r)s (red) and %(g)s'%{'r': ledRedPin, 'g': ledGreenPin} )
    


#Writes something on LCD, a Line has 16 chars
def writeLCD(line1="", line2="" , cursorX=0 , cursorY=0):
    lcd.clear()
    lcd.setCursor(cursorX,cursorY)
    lcd.message( line1 +'\n' )
    lcd.message ( line2 )
    
#simulates opening a box
def openBox(boxId):
    boxId = (boxId-1) %3
    print("Red LED OFF , Green LED ON")
    GPIO.output(ledRedPin[boxId],GPIO.LOW)    #set RedLed OFF
    GPIO.output(ledGreenPin[boxId],GPIO.HIGH) #set GreenLed ON
    
    # Reverse if Bottom A on Keypad is Pressed
    print("Press A to close the box")
    #LCD
    writeLCD("Box open...","A to close Box")
    check = True
    while(check):
        key = keypad.getKey()
        if(key == "A"):
            check = False
    print("Red LED ON , Green LED OFF")
    writeLCD("Box closed...")
    GPIO.output(ledGreenPin[boxId],GPIO.LOW)    #set GreenLed OFF
    GPIO.output(ledRedPin[boxId],GPIO.HIGH)     #set RedLed ON

#Checks if given PIN is correctly entered by user    
def checkPin(userPin,boxPin,boxId):

    StorePin = []
    check = True
    textPin = "PIN: "
    writeLCD("D to confirm",textPin)
    #lets user input the keys
    while(check):
        key = keypad.getKey()
        if(key != keypad.NULL):
            if(key != "A" or key != "B" or key !="C" or key != "#" or key != "*"):
                if(key == "D"):
                    check = False
                else:
                    StorePin.append(key)
                    textPin += key
                    writeLCD("D to confirm",textPin)
    #check if digits Match
    checkPin = True
    checkBoxPin = False
    indexBoxPin = -1
    digits = len(StorePin)
    #recreate Pin from entered Keys
    enteredPin = ""
    for i in range(len(StorePin)):
        enteredPin += StorePin[i]
    print("Entered Pin: ", enteredPin)
        
    if(enteredPin != userPin):
        checkPin = False
            
    if(checkPin):
        print("Entered Pin is right")
        print("Opening Box...")
        writeLCD("PIN correct","Opening Box...")
        time.sleep(2)
        openBox(boxId)
    else:
        for i in range(len(boxPin)):
            if(enteredPin == boxPin[i]):
                checkBoxPin = True
                indexBoxPin = i
                break
        if(checkBoxPin):
            print("Entered Pin is right")
            print("Opening Box...")
            writeLCD("GUESTPIN correct","Opening Box...")
            time.sleep(2)
            #Invalidates GuestKey
            invalidateBoxPin(indexBoxPin,boxId)
            openBox(boxId)
        else:
            print("Entered Pin is wrong")
            writeLCD("Entered Pin is","wrong ...")
            time.sleep(2)

def invalidateBoxPin(index,boxID):
    resp = {} #Dict for response over MQTT
    with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data","r") as fin:
        data = json.load(fin)
    if data:
        for i in range(len(data)):
            if(data[i].get("ID") == boxID):
                boxPin = data[i].get("BoxPIN")
                boxPin[index] = "BEN"
                data[i]["BoxPIN"] = boxPin
                #Creating MQTT response Payload
                resp = data[i]
        with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data", "w") as fout:
		    json.dump(data,fout)
            
        #Create Json Object for MQTT
        json_data = json.dumps(resp)        
        client = mqtt.Client(client_name)
        client.connect(ip, 1883, 60)
        client.publish("invKey"+area, json_data )
        client.disconnect()
        print("successfully updated box data (GUESTKEY)")

def chooseBox():
    writeLCD("Enter Box ID","Confirm with D")
    check = True
    storeBID = []
    text = "ID: "
    while(check):
        key = keypad.getKey()
        if(key != keypad.NULL):
            if(key != "A" or key != "B" or key !="C" or key != "*" or key != "#"):
                if(key == "D" ):
                    check=False
                else:
                    storeBID.append(int(key))
                    text += key
                    print(text)
                    writeLCD("D to confirm",text)
                if(len(storeBID) > 3):
                    writeLCD("Entered ID to","long ...")
                    return -1,-1 ,-1
    writeLCD("...")
    enteredBID = 0
    if(len(storeBID) == 1):
        enteredBID = storeBID[0]
    else:
        for i in range(len(storeBID)):
            enteredBID += 10**(len(storeBID)-i-1) * storeBID[i]
    with open("/home/pi/Desktop/Box_Python_Code/Data/BoxInfo.data","r") as fin:
        data = json.load(fin)
    if data:
        check = True
        for i in range(len(data)):
            if(data[i].get("ID") == enteredBID):
                userPin = data[i].get("PIN").encode("ascii")
                date = data[i].get("Date")
                boxPin = data[i].get("BoxPIN")
                for j in range(len(boxPin)):
                    boxPin[j] = boxPin[j].encode("ascii")
                check = False
                break
        if check:
            tmp = "ID:%d"%enteredBID
            writeLCD("Box with ID: %d"%enteredBID,"does not exist")
            return -1,-1, -1
        
        currentDate = datetime.now()
        date = datetime.strptime(date, "%Y-%m-%d")
        print(currentDate)
        print(date)
        if(currentDate > date):
            print(currentDate)
            writeLCD("Box not rented")
            return -1,-1 ,-1
        
        return userPin,boxPin ,enteredBID
    print("Data error")
    return -1,-1, -1
    
def destroy():
    for i in range(len(ledRedPin)):
        GPIO.output(ledRedPin[i], GPIO.LOW)     # led off
        GPIO.output(ledGreenPin[i], GPIO.LOW)     # led off
    GPIO.cleanup()                     # Release resource
    lcd.clear()
           
if __name__ == '__main__':     # Program start from here
    setupLED()
    try:
        while(1):
            userPin,boxPin,boxId = chooseBox()
            if(userPin == -1):
                time.sleep(5)
            else:
                checkPin(userPin,boxPin,boxId)
        
        print("Exit Programm after 15 sec")
        time.sleep(15)
        destroy()
                    
    except KeyboardInterrupt:  # When 'Ctrl+C' is pressed, the child program destroy() will be  executed.
        destroy()
