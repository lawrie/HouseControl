<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Virgin TiVo remote</title>
<script>

function post(path, params, method) {
    method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}

function remote(key) {
  post('remote.php',{'cmd':'vt ' + key});
}
</script>
</head>
<body>
<img src="tivo.png" alt="" usemap="#Map" />
<map name="Map" id="Map">
    <area alt="home" title="home" href="javascript:remote('home')" shape="rect" coords="49,24,71,40" />
    <area alt="power" title="power" href="javascript:remote('on')" shape="rect" coords="29,34,42,49" />
    <area alt="tv" title="tv" href="javascript:remote('tv')" shape="rect" coords="77,30,93,52" />
    <area alt="guide" title="guide" href="javascript:remote('guide')" shape="rect" coords="18,52,32,66" />
    <area alt="info" title="info" href="javascript:remote('info')" shape="rect" coords="90,53,103,67" />
    <area alt="up" title="up" href="javascript:remote('up')" shape="rect" coords="54,54,65,67" />
    <area alt="left" title="left" href="javascript:remote('left')" shape="rect" coords="35,67,47,84" />
    <area alt="right" title="right" href="javascript:remote('right')" shape="rect" coords="72,71,83,83" />
    <area alt="down" title="down" href="javascript:remote('down')" shape="rect" coords="55,86,67,101" />
    <area alt="ok" title="ok" href="javascript:remote('ok')" shape="rect" coords="53,71,65,81" />
    <area alt="full screen" title="full screen" href="javascript:remote('fs')" shape="rect" coords="18,70,29,84" />
    <area alt="text" title="text" href="javascript:remote('text')" shape="rect" coords="91,70,104,85" />
    <area alt="mute" title="mute" href="javascript:remote('mute')" shape="rect" coords="21,91,31,102" />
    <area alt="subtitles" title="subtitles" href="javascript:remote('subtitles')" shape="rect" coords="88,90,98,105" />
    <area alt="volume up" title="volume up" href="javascript:remote('volume up')" shape="rect" coords="25,109,37,118" />
    <area alt="volume down" title="volume down" href="javascript:remote('volume down')" shape="rect" coords="31,128,42,139" />
    <area alt="my shows" title="my shows" href="javascript:remote('shows')" shape="rect" coords="47,113,73,125" />
    <area alt="channel up" title="channel up" href="javascript:remote('channel up')" shape="rect" coords="84,109,94,120" />
    <area alt="channel down" title="channel down" href="javascript:remote('channel down')" shape="rect" coords="81,126,92,141" />
    <area alt="record" title="record" href="javascript:remote('record')" shape="rect" coords="53,132,67,150" />
    <area alt="thumbs up" title="thumbs up" href="javascript:remote('thumbs up')" shape="rect" coords="34,145,47,162" />
    <area alt="thumbs down" title="thumbs down" href="javascript:remote('thumbs dn')" shape="rect" coords="75,147,86,162" />
    <area alt="play" title="play" href="javascript:remote('play')" shape="rect" coords="48,160,77,179" />
    <area alt="stop" title="stop" href="javascript:remote('stop')" shape="rect" coords="45,217,78,236" />
    <area alt="pause" title="pause" href="javascript:remote('pause')" shape="rect" coords="47,184,73,210" />
    <area alt="fast backwards" title="fast backwards" href="javascript:remote('fb')" shape="rect" coords="30,177,44,212" />
    <area alt="fast forwards" title="fast forwarrds" href="javascript:remote('ff')" shape="rect" coords="78,181,93,210" />
    <area alt="back" title="back" href="javascript:remote('back')" shape="rect" coords="29,227,44,247" />
    <area alt="skip" title="skip" href="javascript:remote('skip')" shape="rect" coords="79,231,89,246" />
    <area alt="slow" title="slow" href="javascript:remote('slow')" shape="rect" coords="48,240,72,258" />
    <area alt="red" title="red" href="javascript:remote('red')" shape="rect" coords="27,256,41,273" />
    <area alt="green" title="green" href="javascript:remote('green')" shape="rect" coords="44,263,57,277" />
    <area alt="yellow" title="yellow" href="javascript:remote('yellow')" shape="rect" coords="62,262,75,277" />
    <area alt="blue" title="blue" href="javascript:remote('blue')" shape="rect" coords="78,256,92,272" />
    <area alt="one" title="1" href="javascript:remote('one')" shape="rect" coords="28,274,45,292" />
    <area alt="two" title="2" href="javascript:remote('two')" shape="rect" coords="53,281,69,298" />
    <area alt="three" title="3" href="javascript:remote('three')" shape="rect" coords="77,278,91,293" />
    <area alt="four" title="4" href="javascript:remote('four')" shape="rect" coords="27,292,44,310" />
    <area alt="five" title="5" href="javascript:remote('five')" shape="rect" coords="52,298,71,317" />
    <area alt="six" title="6" href="javascript:remote('six')" shape="rect" coords="76,293,92,311" />
    <area alt="seven" title="7" href="javascript:remote('seven')" shape="rect" coords="25,310,41,327" />
    <area alt="eight" title="8" href="javascript:remote('eight')" shape="rect" coords="53,318,69,331" />
    <area alt="nine" title="9" href="javascript:remote('nine')" shape="rect" coords="79,313,96,327" />
    <area alt="clear" title="clear" href="javascript:remote('clear')" shape="rect" coords="22,329,38,344" />
    <area alt="zero" title="0" href="javascript:remote('zero')" shape="rect" coords="51,332,70,349" />
    <area alt="last channel" title="last channel" href="javascript:remote('lastch')" shape="rect" coords="81,329,97,345" />
</map>
<?php

include "funcs.php";

$host = "192.168.0.100";
$port = 50000;
// No Timeout
set_time_limit(0);

$cmd = $_POST['cmd'];
if (!empty($cmd)) {
  $result = house($host,$port,$cmd);
  echo "Result: $result";
}

?>
</body>
</html>
