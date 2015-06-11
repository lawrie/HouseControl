<?php
function house($host, $port, $value) {
  $socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create socket\n");

  $result = socket_connect($socket, $host, $port) or die("Could not connect toserver\n");

  $message = "$value\n";

  socket_write($socket, $message, strlen($message)) or die("Could not send data to server\n");

  // get server response
  $result = socket_read ($socket, 1024) or die("Could not read server response\n");
  if (trim($result) == "on") $result="yes";
  else if (trim($result) == "off") $result="no";
  // close socket
  socket_close($socket);
  return $result;
}

function startsWith($haystack, $needle) {
    // search backwards starting from haystack length characters from the end
    return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
}
?>
