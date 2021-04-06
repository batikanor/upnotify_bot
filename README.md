# upnotify_bot
Telegram bot of the upnotify project. use the link t.me/upnotify_bot to contact the bot

## About
Use our homepage to access all publicized data about the bot: https://linkx.tk/upnotify

## Commands
* _Bot will only have access to messages that start with a '/'_
* _Every command can have arguments passed to it. simply put arguments separated with space_

msginfo - see what the bot sees when you send it a message

checksite - checks the site, returns True if HTTP response is 200 (OK)

checkstatic - checks if the given site is **probably** somehow static. Does that by simply checking the website twice within one second. If the body is the same, assumes it is static.


## Important Notices
1) You can't 100% tell that a website is static or dynamic. So the **checkstatic** command 