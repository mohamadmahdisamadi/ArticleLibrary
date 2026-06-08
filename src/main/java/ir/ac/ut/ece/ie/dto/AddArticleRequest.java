package ir.ac.ut.ece.ie.dto;

import java.util.List;

public class AddArticleRequest {
    public String title;
    public String summary;
    public String body;
    public List<String> citations;

    public AddArticleRequest() {}
}