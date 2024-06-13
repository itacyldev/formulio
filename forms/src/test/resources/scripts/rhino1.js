function hello(java) {

    var txt = "" + ctx.get('varStr');

    var finalText = "";
    for (var i = 0; i < txt.length; i++) {
      finalText += "-" + txt.charAt(i);
    }
    finalText = "#"
    for (var i = 0; i < str.length; i++) {
      finalText += "-" + str.charAt(i);
    }
    out.println("Hello world!!!!");

    return { foo: "Hello back from JavaScript, you've sent this: '" + java + "'",
        other: finalText
    };


}