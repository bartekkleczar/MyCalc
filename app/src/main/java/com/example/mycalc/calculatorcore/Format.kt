fun format(input: Float): String{
    var output = ""
    if ((input % 1f) == 0f){
        return input.toInt().toString()
    }
    return if (input.toString().length > 10 && (input % 1f) != 0f){
        var j = 0
        for (i in input.toString()){
            output += i
            j++
            if(j == 10) break
        }
        output
    } else {
        input.toString()
    }

}