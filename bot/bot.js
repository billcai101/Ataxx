console.log('The bot is starting');
 
var Twit = require('twit');
 
var T = new Twit({
  consumer_key:         '',
  consumer_secret:      '',
  access_token:         '',
  access_token_secret:  '',
  timeout_ms:           60*1000,  // optional HTTP request timeout to apply to all requests.
});
 
var search_queries = ['retweet to win', 'RT to win', '#contest', '#giveaway', '#giveaways', '#win', '#freebiefriday', '#winitwednesday', '#winwednesday', '#competition']
 
var following_params = {
    screen_name: '', // your screen name here
    count: 200
}
 
var retweeting_params = {
    screen_name: '', // your screen name here
    count: 200
}
 
var following_list = [];
var retweet_list = [];
 
var following_messup = 0;
var retweeting_messup = 0;
 
function main() {
    T.get('friends/list', following_params, function(err, data, response) {
        var users = data.users;
        for (var i = 0; i < users.length; i++) {
            following_list.push(users[i].screen_name);  //This is so we don't accidently refollow people we are already following.
        }  
    });
 
    T.get('statuses/user_timeline', retweeting_params, function(err, data, response) {
        for (var i = 0; i < data.length; i++) {
            retweet_list.push(data[i].id_str); // This is so we don't accidently retweet tweets we have already retweeted.
        }
 
    });

    setTimeout(function() {
    for (var i = 4; i < 6; i++) {
     var params = {q:search_queries[i], count:15, lang: 'en', result_type: 'popular'};
     T.get('search/tweets', params, function(err, data, response) {
         var tweets = data.statuses;
         for (var i = 0; i < tweets.length; i++) {
             retweet_checker(tweets[i].id_str);
             follow_checker(tweets[i].user.screen_name);
         }
     });
    }}, 10000)
 
    setTimeout(function() {
    console.log('following_messup');
    console.log(following_messup);}, 40000);
    setTimeout(function() {
    console.log('retweeting_messup');
    console.log(retweeting_messup);}, 41000);
 
}

 
function follow_checker(screen_name) {
    if (following_list.indexOf(screen_name) > -1) {
        console.log('Already following ' + screen_name);
    } else {
        follow(screen_name);
    }
}
 
function retweet_checker(id) {
    if (retweet_list.indexOf(id) > -1) {
        console.log('Already retweeted ' + id);
    } else {
        retweet(id);
    }
}
 
 
function retweet(identification) {
    T.post('statuses/retweet/:id', {id: identification}, function(err,data,response) {
        if (err) {
            console.log(err.message);
            console.log('You messed up retweet ' + identification);
            retweeting_messup += 1;
        } else {
            console.log('It worked');
        }
    })
 
}
 
 
function follow(id) {
    T.post('friendships/create', {screen_name: id }, function(err, data, response) {
        if (err) {
            console.log(err.message);
            console.log('You messed up follow ' + id);
            following_messup += 1;
        } else {
            console.log('Successfully following ' + id);
        }
    } )
}
p