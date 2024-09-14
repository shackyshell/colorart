package com.shacky.colorart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WebScraperService {

    private static final String URL = "https://www.parismuseescollections.paris.fr/fr/recherche/type/oeuvre/types-objet/330958";

    public List<String> scrapeImageUrls(Optional<String> url) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        Document document = Jsoup.connect(url.orElse(URL)).get();

        for (Element img : document.select("img[src]")) {
            String imgUrl = img.absUrl("src");
            imageUrls.add(imgUrl);
        }

        return imageUrls;
    }
}
