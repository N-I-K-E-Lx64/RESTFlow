import {Component, OnDestroy, OnInit} from '@angular/core';
import {Message} from '@stomp/stompjs';
import {RxStompService} from "@stomp/ng2-stompjs";
import {Subscription} from "rxjs";


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  private subscription: Subscription | undefined;

  constructor(private rxStompService: RxStompService) {
  }

  ngOnInit(): void {
    this.subscription = this.rxStompService.watch('/user/queue/specific-user').subscribe((message: Message) => {
      console.log(message.body);
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  onSendMessage(): void {
    const message = {from: 'webapp', to: 'server', text: 'HelloWorld'};
    this.rxStompService.publish({destination: '/app/testEndpoint', body: JSON.stringify(message)});
  }
}
