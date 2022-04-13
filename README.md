# th2-conn-qfj
 This microservice allows you to send and receive messages over the FIX protocol
  
## Configuration
- grpcStartControl - enables start/stop control via [gRPC service](https://github.com/th2-net/th2-grpc-conn/blob/master/src/main/proto/th2_grpc_conn/conn.proto#L24) (`false` by default)
- autoStart - start service automatically (`true` by default and if `startControl` is `false`)
- autoStopAfter - stop after N seconds if the service was started automatically prior to send (`0` by default which means disabled)
- sessionsSettings - list with sessions settings for QuickFix:

     | Name of tag | Description | Valid Values| Default value|
     | :----: | :----: | :----: | :----: |
     | BeginString | Version of FIX this session should use |	FIX.4.4, FIX.4.3, FIX.4.2, FIX.4.1, FIX.4.0, FIXT.1.1 (which then requires DefaultApplVerID, see below)||
     | SenderCompID | Your compID as associated with this FIX session	|case-sensitive alpha-numeric string||
     | SenderSubID|(Optional) Your subID as associated with this FIX session	|case-sensitive alpha-numeric string||
     | SenderLocationID| (Optional) Your locationID as associated with this FIX session	|case-sensitive alpha-numeric string||
     | TargetCompID	| Counterparty's compID as associated with this FIX session	| case-sensitive alpha-numeric string	| | 
     | TargetSubID	| (Optional) Counterparty's subID as associated with this FIX session	| case-sensitive alpha-numeric string	|| 
     | TargetLocationID	| (Optional) Counterparty's locationID as associated with this FIX session |	case-sensitive alpha-numeric string	 | | 
     | DefaultApplVerID |	Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.	| String. Examples:	FIX 5.0 over FIXT 1.1 DefaultApplVerID=7 # BeginString: FIX 5.0 over FIXT 1.1	DefaultApplVerID=FIX.5.0	# BeginString: FIX 4.2 over FIXT 1.1	DefaultApplVerID=FIX.4.2	|No default. Required for FIXT 1.1 |
     | ConnectionType|	Defines if session will act as an acceptor or an initiator|	initiator / acceptor|  |
     | ValidateUserDefinedFields |	If set to N, user defined fields (field with tag >= 5000) will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.	| true / false|	true |
     | ValidateIncomingMessage	| Allow to bypass the message validation (against the dictionary).	| true / false	| true |
     | CheckLatency	| If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.	| true / false| 	Y|
     | ReconnectInterval	| Time between reconnection attempts in seconds. Only used for initiators	| positive integer	| 60 |
     | HeartBtInt | 	Heartbeat interval in seconds. Only used for initiators. |	positive integer	 | 30 | 
     | LogonTimeout |	Number of seconds to wait for a logon response before disconnecting.	| positive integer |	10 |
     | LogoutTimeout |	Number of seconds to wait for a logout response before disconnecting.	| positive integer | 2 |
     | NonStopSession	| If set the session will never reset. This is effectively the same as setting 00:00:00 as StartTime and EndTime. |	true / false|	false |
     | SocketConnectPort |	Socket port for connecting to a session. Only used with a SocketInitiator | positive integer | |	 
     | SocketConnectHost | Host to connect to. Only used with a SocketInitiator	| valid IP address in the format of x.x.x.x or a domain name	 | | 
     | SocketConnectProtocol |	Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.	| "TCP" or "VM_PIPE". |	"TCP" |
     | SocketConnectPort<n>	| Alternate socket port(s) for connecting to a session for failover or load balancing, where falseis a positive integer, i.e. SocketConnectPort1, SocketConnectPort2, etc. Must be consecutive and have a matching SocketConnectHost<n> |	positive integer	 | | 
     | SocketConnectHost<n> | 	Alternate socket host(s) for connecting to a session for failover or load balancing, where n is a positive integer, i.e. SocketConnectHost1, SocketConnectHost2, etc. Must be consecutive and have a matching SocketConnectPort<n> Connection list iteration rules: Connections are tried one after another until one is successful: SocketConnectHost:SocketConnectPort, SocketConnectHost1:SocketConnectPort1, etc. Next connection attempt after a successful  connection will start at first defined connection again: SocketConnectHost:SocketConnectPort. | valid IP address in the format of x.x.x.x or a domain name	 |
     |FileStorePath |	Directory to store sequence number and message files. Only used with FileStoreFactory.	| valid directory for storing files, must have write access	 | | 
     |FileLogPath |	Directory to store logs. Only used with FileLogFactory. |	valid directory for storing files, must have write access	 ||
     |RefreshOnLogon	| Refresh the session state when a Logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).	| true / false	| false|
     | ResetOnLogon|	Determines if sequence numbers should be reset before sending/receiving a logon request.	| true / false	| false|
     | ResetOnLogout	| Determines if sequence numbers should be reset to 1 after a normal logout termination. |	true / false|	false |
     | ResetOnDisconnect	|Determines if sequence numbers should be reset to 1 after an abnormal termination.	| true / false| false|
     | ResetOnError	| Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.	| true / false|	false |
     | DisconnectOnError	| Session setting for doing an automatic disconnect when an error occurs.	| true / false	| false|
     | RequiresOrigSendingTime | If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected. | true / false | true |
     | IgnoreAbsenceOf141tag | Ignore the absence of ResetSeqNumFlag(141) tag in the received Logon(A) message | true / false | false |
     | SeqNumSender | Sender initial sequence number. It is applied when the service starts. | positive integer | |
     | SeqNumTarget | Target initial sequence number. It is applied when the service starts. | positive integer | |
     | DuplicateTagsAllowed | If set no reject will be sent on incoming message with duplicate tags | true / false | false |
     | ValidateFieldsOutOfRange | If is set to false (unchecked), messages which contain values not presented in the range will not be rejected| true / false | true |
     | CheckRequiredTags |  If is set to true, it will check required tags | true / false | true |
     | PersistMessages | If set to false this will cause the Session to not persist any messages and all resend requests will be answered with a gap fill.| true / false | true |
     | SeqNumFromRejectRegexp | Regexp to extract the sequence number from the Reject(3) message. Sequence number must be included in the group named <b>sequence</b>. For example regexp <b>Expected Sequence:(?<sequence>[\\d]+) Received:[\\d]+</b> extracts <b>1807</b> from text <b>Expected Sequence:1807 Received:13</b>| case-sensitive alpha-numeric string | |
     | SeqNumFromLogoutRegexp | Regexp to extract the sequence number from the Logout(5) message. Sequence number must be included in the group named <b>sequence</b>. For example regexp <b>Expected Sequence:(?<sequence>[\\d]+) Received:[\\d]+</b> extracts <b>1807</b> from text <b>Expected Sequence:1807 Received:13</b>| case-sensitive alpha-numeric string | |
     | UseDefaultApplVerID | The tag DefaultApplVerID(1137) might be used in Logon message or not | case-sensitive alpha-numeric string | |
     | DefaultCstmApplVerID | The value of the tag DefaultCstmApplVerID(1408) that is used in Logon(A) message | case-sensitive alpha-numeric string | |
     | Autorelogin | Create a new session after disconnecting on the server side | true / false | true |
     | OrderingFields | It enables fields ordering in the raw message by dictionary (when changing fields numbers) | true / false | |
     | FakeResendRequest | Response to ResendRequest(2) by heartbeats | true / false | false |
     | EnableNextExpectedMsgSeqNum | Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions < 4.4. Only works when ValidateSequenceNumbers is enabled. | true / false | false |
     | TimeZone | Time zone for this session; if specified, the session start and end will be converted from this zone to UTC. | Time zone ID (America/New_York, Asia/To kyo, Europe/London, etc.) ||
     | StartDay | For week long sessions, the starting day of week for the session. Use in combination with StartTime. Incompatible with Weekdays | Day of week in the default locale (e.g. Monday, mon, lundi, lun. etc.) ||
     | EndDay | For week long sessions, the ending day of week for the session. Use in combination with EndTime. Incompatible with Weekdays | Day of week in the default locale (e.g. Monday, mon, lundi, lun. etc.) ||
     | StartTime | Time of day that this FIX session becomes activated | time in the format of HH:MM:SS [timezone]. The time zone is optional. The TimeZone setting will be used, if set, or UTC will be used by default. The timezone string should be one that the Java TimeZone class can resolve. For example, "15:00:00 US/Central". ||
     | EndTime | Time of day that this FIX session becomes deactivated | time in the format of HH:MM:SS [timezone]. The time zone is optional. The TimeZone setting will be used, if set, or UTC will be used by default. The timezone string should be one that the Java TimeZone class can resolve. For example, "15:00:00 US/Central". ||
     | TimeStampPrecision | Determines precision for timestamps in (Orig)SendingTime fields. Only available for FIX.4.2 and greater. NB: This configuration is only considered for messages that are sent out. QuickFIX/J is able to receive UtcTimestamp fields with up to picosecond precision. Please note however that only up to nanosecond precision will be stored, i.e. the picoseconds will be truncated.| One of: SECONDS, MILLIS, MICROS, NANOS | MILLIS |
     | SessionAlias| session alias for incoming/outgoing th2 messages. | case-sensitive alpha-numeric string | |
     | QueueCapacity | maximum size of the session message queue | integer value | |
    
	
We can also put these settings in the root directory to set the default session settings.
		


### The service will automatically connect before sending the message, if it was not connected

## MQ pins
- input queue with `subscribe` and `send` attributes for outgoing messages
- output queue with `publish`, `first` (for incoming messages) or `second` (for outgoing messages) attributes

## Inputs/outputs
This section describes the messages received and produced by the service

**Inputs**
This service receives messages that will be sent via MQ as `MessageGroups`, containing a single `RawMessage` with a message body

**Outputs**
Incoming and outgoing messages are sent via MQ as `MessageGroups`, containing a single `RawMessage` with a message body.
	
## Deployment via infra-mgr
	
Here's an example of infra-mgr config required to deploy this service.  
	  
	  
```yaml
apiVersion: th2.exactpro.com/v1
kind: Th2Box
metadata:
  name: conn-qfj
spec:		
  image-name: ghcr.io/th2-net/th2-conn-qfj
  image-version: 0.0.1-fix-client-1922181221
  custom-config:
    grpcStartControl: true
    autoStart: true
    autoStopAfter: 0
    fileStorePath: storage/messages/
    fileLogPath: outgoing
    connectionType: initiator
    reconnectInterval: 60
    heartBtInt: 30
    useDataDictionary: Y
    nonStopSession: Y
    sessionsSettings:
      -  
        beginString: FIXT.1.1
        defaultApplVerID: 9
        socketConnectHost: 10.119.1.45
        socketConnectPort: 32104
        senderCompID: client2
        targetCompID: server
        sessionAlias: client2
        appDataDictionary: FIX.5.0.xml
        transportDataDictionary: FIXT.1.1.xml
      -
        beginString: FIX.4.2 
        socketConnectHost: 10.119.1.45
        socketConnectPort: 32102
        senderCompID: client1
        targetCompID: server
        sessionAlias: client1
        dataDictionary: FIX.4.2.xml
  type: th2-conn
  pins:
    - name: to_send
      connection-type: mq
      attributes:
        - subscribe
        - send
    - name: outgoing_messages
      connection-type: mq
      attributes:
        - second
        - publish
        - raw
    - name: incoming_messages
      connection-type: mq
      attributes:
        - first
        - publish
        - raw
  extended-settings:
    service: 
      enabled: true
    resources:
      limits:
        memory: 400Mi
        cpu: 800m
      requests:
        memory: 100Mi
        cpu: 20m
```
