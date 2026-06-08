package ir.ac.ut.ece.ie.dto;

import java.util.List;

public class EditArticleRequest {
    public String title;
    public String summary;
    public String body;
    public List<String> citations;

    public EditArticleRequest() {}
}
