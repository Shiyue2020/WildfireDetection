from firebase import firebase

firebase = firebase.FirebaseApplication('https://wildfiredetection-fdd86.firebaseio.com/')
result = firebase.get('/Australia', None)
print(result)
