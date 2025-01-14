export class ScoreUser {
  userId: number;
  username: string;
  roles: string[];
}

export class UserToken {
  username: string;
  roles: string[];
  authentication: string;
  enabled: boolean;

  constructor() {
    this.roles = ['',];
    this.authentication = '';
    this.username = 'unknown';
    this.enabled = false;
  }
}

export class OAuth2AppInfo {
  loginUrl: string;
  providerName: string;
  displayProviderName: string;
  backgroundColor: string;
  fontColor: string;
}
