package com.example.Crypto.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CoindeskNews {


    public String getCoindeskNews() throws IOException {
        String url = "https://www.coindesk.com/";
        try {
            Document doc = Jsoup.connect(url).get();
            //System.out.println(doc.title()); // выводим заголовок страницы
            Element newsHeadline = doc.selectFirst(".card-title"); // выбираем все элементы с классом "article-card"
            Element newsText = doc.selectFirst("p.typography__StyledTypography-owin6q-0.bZvpZH");
            String result = newsHeadline.text() + "\n" +
                    newsText.text();

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "^(";
    }

}

