# Creating a Forground Android Service

## Summary

This application starts a background service that simply increments a variable each second, simluating a large download.  Since API 26, developers are unable to run *persistent* background services without displaying to the user that said service is running.  This is done through a **Notification** that displays a **Progress Bar**.  Once the "Download" is complete, the user can click the notification and the **Main Actvity** will open and the notification will be dismissed.  This will *not* start another service.  If the user clicks the **Restart Service** button, the service will restart regardless of how far along it is.

## Restart Service

Rather than reset the state of the service, the button will destroy the service and recreate it using the **SensorReseterBroadcastReciever**.  
