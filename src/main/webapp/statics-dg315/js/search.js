function OnSearchCheckAndSubmit(path) {
    var keyword = document.getElementById("search_keyword").value;
    if (keyword == '' || keyword == null) {
        alert("请填写您想搜索的关键词");
        return;
    }
    else {        
        window.location = path + "Search.aspx?Keyword=" + encodeURI(keyword);
    }
}