import {InjectableRxStompConfig} from "@stomp/ng2-stompjs";

export const rxStompConfig: InjectableRxStompConfig = {
  // Server
  brokerURL: 'ws://localhost:8080/restflow',

  // Interval in milliseconds, set to 0 to disable
  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,

  // Wait in milliseconds before attempting auto reconnect
  reconnectDelay: 5000,

  debug: (msg: string): void => {
    console.log(new Date(), msg);
  },
};
