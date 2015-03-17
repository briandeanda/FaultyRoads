var map, heatmap;

  function initialize() {
    var lat = 0;
    var lon = 0;
    /*
    navigator.geolocation.getCurrentPosition(foc);
    function foc(position){
        lat = position.coords.latitude;
        lon = position.coords.longitude;
        console.log("latitude: " + lat);
        console.log("longitude: " + lon);
    }
    */
    var mapOptions = {
      zoom: 12,
      center: new google.maps.LatLng(36.651068, -121.778641),
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  var pointArray = new google.maps.MVCArray();
  //var Firebase = require("firebase");
  var ref = new Firebase("https://faulty-roads.firebaseio.com/");

    // Retrieve new posts as they are added to Firebase
    ref.on("child_added", function(snapshot) {
      var newPost = snapshot.val();
        coordinates = new google.maps.LatLng(newPost.latitude,newPost.longitude)
        pointArray.push(coordinates)
    });

  heatmap = new google.maps.visualization.HeatmapLayer({
    data: pointArray,
    dissipating: true,
    map: map
  });

  heatmap.setMap(map);
   heatmap.set('radius', heatmap.get('radius') ? null : 1);
}

google.maps.event.addDomListener(window, 'load', initialize);
