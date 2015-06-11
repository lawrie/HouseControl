<!DOCTYPE html>
<?php
$host = "housecontrol";
$port = 50000;
// No Timeout 
set_time_limit(0);

include "funcs.php";

$array = array(
    "Total" => "power",
    "Dish washer" => "dishwasher value",
    "Washer" => "washer value", 
    "Dryer" => "dryer value", 
    "Games" => "xbox value", 
    "Bedroom media" => "bedmedia value", 
    "Temperature" => "temperature 1",
    "Humidity" => "humidity 1",
    "Occupied" => "occupied 1",
    "Light level" => "lightlevel 1",
    "Left light" => "world status",
    "Right light" => "periodic status",
    "Temperature (front)" => "temperature 3",
    "Temperature (back)" => "temperature 2",
    "Light level (front)" => "lightlevel 3",
    "Light level (back)" => "lightlevel 2",
    "Occupied (front)" => "occupied 3",
    "Occupied (back)" => "occupied 2",
    "Front light" => "pollock status",
    "Back light" => "picasso status",
    "Couch weight" => "humidity 8",
    "Camera socket" => "spy status",
    "Back window" => "switch 2 status",
    "Table socket" => "coffee status",
    "Computer socket" => "print status",
    "Guitar socket" => "guitar status",
    "Banjolele socket" => "banjolele status",
    "Fan socket" => "fan status",
    "Bench socket" => "bench status",
    "Tablet socket" => "trans status",
    "Letter box" => "switch 1 status",
);
?>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="10" >
<title>House Control Dashboard</title>
<style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
    align: center;
    margin : auto;
}

th {
    text-align: left;
}

td {
    font-family: Times;
}

div {
    margin: 10px;
}

.data {
    border: 1px solid black;
    padding: 10px;
    width: 300px;
}

#livingroom {
    position: absolute;
    left: 360px;
    top: 58px;
}

#form {
    position: absolute;
    left: 360px;
    top: 560px;
}

body {
    background: radial-gradient(blue, cyan, blue);
    font-family: Arial;
}

h3 {
    text-align: center;
    margin: 0px 0px 10px 0px;
}
</style>
</head>
<body>
<h2>House data</h2>
<div id="power" class="data">
<h3>Power</h3>
<table>
  <tr>
    <th>Name</th>
    <th>Value</th> 
  </tr>
<?php

foreach ($array as $i => $value) {
  if ($i == "Temperature") {
?>
</table>
</div>
<div id="masterbedroom" class="data">
<h3>Master bedroom</h3>
<table>
  <tr>
    <th>Name</th>
    <th>Value</th> 
  </tr>
<?php
  } else if ($i == "Temperature (front)") {
?>
</table>
</div>
<div id="livingroom" class="data">
<h3>Living room</h3>
<table>
  <tr>
    <th>Name</th>
    <th>Value</th> 
  </tr>
<?php
  } else if (startsWith($i,"Letter")) {
?>
</table>
</div>
<div id="hall" class="data">
<h3>Hall</h3>
<table>
  <tr>
    <th>Name</th>
    <th>Value</th> 
  </tr>
<?php
  }
  $result = house($host, $port, $value);
  echo "<tr><td>".$i."</td><td style='text-align:right'>".$result."</td></tr>";
}
?>
</table>
</div>
</body>
</html>


