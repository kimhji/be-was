package webserver.parse;

import common.Config;

public class PageStruct {
    String header;
    String post;
    String comment;

    PageReplacer headerContent;
    PageReplacer postContent;
    PageReplacer commentContent;
    public PageStruct(){
        headerContent = new PageReplacer();
        postContent = new PageReplacer();
        commentContent = new PageReplacer();
        init();
    }

    public void setState(String path, boolean isLogin){
        header = headerContent.getParsedPage(path, isLogin);
        post = postContent.getParsedPage(path, isLogin);
        comment = commentContent.getParsedPage(path, isLogin);
    }

    private void init(){
        headerContent.setPage(false, PageReplacer.PageType.DEFAULT,  Config.PAGE_HEADER_NOT_LOGIN);
        headerContent.setPage(true, PageReplacer.PageType.DEFAULT, Config.PAGE_HEADER_LOGIN);
        headerContent.setPage(false, PageReplacer.PageType.ARTICLE, Config.PAGE_HEADER_NOT_LOGIN);
        headerContent.setPage(true,PageReplacer.PageType.ARTICLE, Config.PAGE_HEADER_LOGIN);
        headerContent.setPage(true,PageReplacer.PageType.COMMENT, Config.PAGE_HEADER_LOGIN);
    }
}
