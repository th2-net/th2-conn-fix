# th2-conn-fix
 This microservice allows you to send and receive messages over the FIX protocol
  
## Configuration
- grpcStartControl - enables start/stop control via [gRPC service](https://github.com/th2-net/th2-grpc-conn/blob/master/src/main/proto/th2_grpc_conn/conn.proto#L24) (`false` by default)
- autoStart - start service automatically (`true` by default and if `startControl` is `false`)
- autoStopAfter - stop after N seconds if the service was started automatically prior to send (`0` by default which means disabled)
- sessionsSettings - list with sessions settings for QuickFix (e.g. BeginString, senderCompID, targetCompID etc)

Service will also automatically connect prior to message send if it wasn't connected

## MQ pins
- input queue with `subscribe` and `send` attributes for outgoing messages
- output queue with `first` (for incoming messages) or `second` (for outgoing messages) attributes

## Inputs/outputs
This section describes the messages received and produced by the service

**Inputs**
This service receives messages that will be sent via MQ as `MessageGroups`, containing a single `RawMessage` with a message body

**Outputs**
Incoming and outgoing messages are sent via MQ as `MessageGroups`, containing a single `RawMessage` with a message body.
