# Description
OsmFeedback allows to add multiple Feedbacks into the Firebase Database used by the app Feedbackr.
This allows to quickly insert a huge amount of data for testing.

# Example
<pre>
(
node ["amenity"="college"]
    (51.184023859236,6.7480945587158,51.224517824063,6.8159866333008);
node ["amenity"="library"]
    (51.184023859236,6.7480945587158,51.224517824063,6.8159866333008);
);
out;
</pre>

You can use https://overpass-turbo.eu/ to create a OverpassQL query (Export > Query > OverpassQL)
Amenity options can be found at https://wiki.openstreetmap.org/wiki/DE:Key:amenity