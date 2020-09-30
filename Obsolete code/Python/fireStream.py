import json
import tweepy as tw
from tweepy import StreamListener
from tweepy import Stream
from tweepy.auth import OAuthHandler


consumer_key= 'Your_Consumer_Key_Here'
consumer_secret= 'Your_Consumer_Secret_Here'
access_token= 'Your_Access_Token_Here'
access_token_secret= 'Your_Access_Token_Secret_Here'

#fire_list = []
count = 0

class StreamListener(tw.StreamListener):
    
    def on_data(self, data):
        global count

        if count <= 100:

            JSONdata = json.loads(data)

            geo = JSONdata["geo"]

            if geo is not None:

                if JSONdata["user"]:
                    print("user: ", JSONdata["user"]["screen_name"])

                if JSONdata["created_at"]:
                    print("date: ", JSONdata["created_at"])
            
                if JSONdata["place"]:
                    print("place: ", JSONdata["place"]["full_name"])

                if JSONdata["geo"]:
                    print("geo: ", JSONdata["geo"])

                    count += 1
            
                return True
        
            #coords = JSONdata['coordinates']
            
            #if coords is not None:
            #    print (coords['coordinates'])
            #    lon = coords['coordinates'][0]
            #    lat = coords['coordinates'][1]
            #
            #    fire_list.append(JSONdata) 

                 
        else:
            return False

    def on_error(self, status_code):
        if status_code == 420:
            return False

#This will take some time to collect data
if __name__=="__main__":
    listener = StreamListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    twitterStream = Stream(auth, listener)
    #Search words
    twitterStream.filter(track=["wildfire", "Australia"])
    
