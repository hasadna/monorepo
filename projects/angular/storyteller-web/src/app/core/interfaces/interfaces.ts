// We use EasyStory to have one container for all data, which is used on Story Component.
// It's much easier, than to send several variables.
// Also when EasyStory is created, some data already is converted to more practical way.
// E.g. username: (user.getFirstName() + ' ' + user.getLastName()).trim()
export interface EasyStory {
  storyId: string;
  momentId: string;
  username: string;
  imageURL: string;
  email: string;
  projectId: string;
  timestamp: number;
  oneliner: string;
  note: string;
  screenshotName: string;
  screenshotURL?: string;
}
