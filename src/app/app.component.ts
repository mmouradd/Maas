import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DevToolbarComponent } from './shared/components/dev-toolbar/dev-toolbar.component';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, DevToolbarComponent],
  template: `
    <router-outlet />
    @if (!isProd) { <app-dev-toolbar /> }
  `,
})
export class AppComponent {
  isProd = environment.production;
}
