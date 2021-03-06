# Project 3 - *Instagram*

**Instagram** is a photo sharing app using Parse as its backend.

Time spent: **25** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication.
- [x] User can log in and log out of his or her account.
- [x] The current signed in user is persisted across app restarts.
- [x] User can take a photo, add a caption, and post it to "Instagram."
- [x] User can view the last 20 posts submitted to "Instagram."
- [x] User can pull to refresh the last 20 posts submitted to "Instagram."
- [x] User can tap a post to view post details, including timestamp and caption.

The following **stretch** features are implemented:

- [x] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [x] User should switch between different tabs - viewing all posts (feed view), capture (camera and photo gallery view) and profile tabs (posts made) using a Bottom Navigation View.
- [x] User can load more posts once he or she reaches the bottom of the feed using infinite scrolling.
- [x] Show the username and creation time for each post.
- [x] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse.
- User Profiles:
  - [x] Allow the logged in user to add a profile photo.
  - [x] Display the profile photo with each post.
  - [x] Tapping on a post's username or profile photo goes to that user's profile page.
- [x] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.
- [ ] Create a custom Camera View on your phone.
- [x] Run your app on your phone and use the camera to take the photo.

The following **additional** features are implemented:

- [x] The sign up screen has password confirmation functionality.
- [x] The state of the home, compose, and settings fragments are saved, and are not refreshed upon opening a new tab in the BottomNavigationView.
- [x] The user can open the camera roll to upload a profile picture.
- [x] The photos on a user's profile are displayed in a grid layout.
- [x] Comments within a post's details view have a relative timestamp.
- [x] Tapping on a comment's username or profile photo goes to that user's profile page.

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1. I would like to discuss other possible implementations of liking and commenting, using different models of ParseObjects.
2. I would also like to discuss more about how to efficiently carry out parse queries, if our databases were larger.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/jkallini/Instagram/blob/master/walkthrough1.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />
<img src='https://github.com/jkallini/Instagram/blob/master/walkthrough2.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Credits

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Implementing likes and comments was the most challenging part of the assignment - it really required a lot of thought regarding what kind of ParseObjects to use to implement them.

## License

    Copyright 2019 Julie Kallini

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
