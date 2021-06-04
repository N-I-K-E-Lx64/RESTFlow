import {Component, OnInit} from '@angular/core';

interface NavDrawerRoute {
  icon?: string;
  route?: string;
  title?: string;
}

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  public navRoutes: NavDrawerRoute[];

  constructor() {
    this.navRoutes = [];
  }

  ngOnInit(): void {
    this.navRoutes = [{icon: 'build', route: "", title: "Modeling"}];
  }

}
