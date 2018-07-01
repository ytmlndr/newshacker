# Newshacker

RESTful API for creating posts and upvoting or downvoting a post
1. A user can create a new post providing its text.
2. A user can update an existing post’s text.
3. A user can upvote or downvote a post.
4. A user can receive the front page comprised of a list of “Top Posts”.

#### API
| Method | Resource | Description | Request | Notes
| :--- | :--- | :--- | :--- | --- |
| POST | /post | Create a new post | `{"text":"test", "userId":123}`
| PUT | /post/{postId} | Update a post's text | `{"text":"test1", "userId":123}`
| POST | /post/{postId}/vote | Create a vote on post | `{"voteType":"Upvote", "userId":1234}` | voteType values: Upvote or Downvote 
| GET | /post?size={size} | Get top posts | 

#### Build
1. Build the jar:<br>
`mvn clean install -Dmaven.test.skip=true`
2. Compose with Docker:<br>
`docker-compose build`<br>
`docker-compose up`

