# BlueKa_SoundSync (Brief Explanations)
The application displays the atomic time and the system time.(Functions like a digital clock)\  
This is done using a thread that is updated every 10 milliseonds.

## Using atomic time and system time to find offset
 Atomic Time : Precise time obtained by NTP servers\
 System Time : Phone system time\
 Offset : Difference between atomic time and system time. We also have to check whether the value of offset is positive or negative.\
 
NTP server used : "pool.ntp.org" 
Other NTP servers can be obtained [here](https://www.mindprod.com/jgloss/timesources.html).
 
## Using offset in BlueKa app to play music across multiple devices synchronously
<img src="https://github.com/Group-10b-SE-GP/BlueKa_SoundSync/blob/master/SmartSelect_20210208-162016_Samsung%20Notes.jpg">
