import json
import tweepy as tw

consumer_key= 'Your_Consumer_Key_Here'
consumer_secret= 'Your_Consumer_Secret_Here'
access_token= 'Your_Access_Token_Here'
access_token_secret= 'Your_Access_Token_Secret_Here'


auth = tw.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
api = tw.API(auth)


#Declare the search terms and date 
search_words = "#Wildfire" + "Australia"

#Filter the retweets
new_search = search_words + " -filter:retweets"

date_since = "2020-03-03"


#Collect tweets with .Cursor
tweets = tw.Cursor(api.search,
              q=new_search,
              lang="en",
              since=date_since).items(20)
tweets


for tweet in tweets:
    user = tweet.user.screen_name
    date = tweet.created_at
    location = tweet.user.location
    geoEnb = tweet.user.geo_enabled
    coords = tweet.coordinates
    #geo = tweet.geo
    
    #if (geoEnb == True):
    #    coords = json.dumps(coords)

    print(user, date, location, geoEnb, coords)




