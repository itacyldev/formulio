//log.println("Start Script Prompt Dependencies");
var f1 = function (x,y){

    return x + y;
};

var f2 = function(x){
    return 2*x;
};


//log.println(f1(22,22));

f2(ctx.var1);
f1(ctx.var1, ctx.var2);