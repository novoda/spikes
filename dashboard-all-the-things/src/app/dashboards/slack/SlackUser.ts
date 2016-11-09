export class SlackUser {
  
  public name: string;
  public realName: string;
  public title: string;
  public imageUrl: string;

  constructor(user) { 
    this.name = user.name;
    this.realName = user.real_name;
    this.title = user.profile.title;
    this.imageUrl = user.profile.image_512;
  }

}
