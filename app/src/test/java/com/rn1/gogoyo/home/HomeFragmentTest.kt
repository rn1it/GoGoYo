package com.rn1.gogoyo.home

import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Users
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class HomeFragmentTest {

    private val articleDataList = mutableListOf<Articles>()

    @Before
    fun setArticleData(){

        val gary = Users(id = "001", name = "Gary")
        val emil = Users(id = "002", name = "Emil")
        val louis = Users(id = "003", name = "Louis")
        val sylvie = Users(id = "004", name = "Sylvie")
        val taiYi = Users(id = "005", name = "Tai-Yi")
        val johnny = Users(id = "006", name = "Johnny")
        val eric = Users(id = "007", name = "Eric")
        val nicole = Users(id = "008", name = "Nicole")
        val tron = Users(id = "009", name = "Tron")

        val article1 = Articles(title = "Happy new year", author = gary)
        val article2 = Articles(title = "Let's ToneGo", author = taiYi)
        val article3 = Articles(title = "Gourmandism", author = louis)
        val article4 = Articles(title = "Table Gamer", author = sylvie)
        val article5 = Articles(title = "Trip Trip", author = emil)
        val article6 = Articles(title = "StarUp Gang", author = eric)
        val article7 = Articles(title = "Mine For Yours", author = johnny)
        val article8 = Articles(title = "譜譜", author = tron)
        val article9 = Articles(title = "GoGoYo", author = gary)
        val article10 = Articles(title = "PetNanny", author = nicole)

        articleDataList.add(article1)
        articleDataList.add(article2)
        articleDataList.add(article3)
        articleDataList.add(article4)
        articleDataList.add(article5)
        articleDataList.add(article6)
        articleDataList.add(article7)
        articleDataList.add(article8)
        articleDataList.add(article9)
        articleDataList.add(article10)
    }

    @Test
    fun testFilterCorrect() {

        val home = HomeFragment()
        val resultList = home.filter(articleDataList, "gary")
        val expectList = mutableListOf<Articles>()

        val gary = Users(id = "001", name = "Gary")

        val article1 = Articles(title = "Happy new year", author = gary)
        val article9 = Articles(title = "GoGoYo", author = gary)
        expectList.add(article1)
        expectList.add(article9)

        Assert.assertEquals(expectList, resultList)
    }

    @Test
    fun testFilterFail() {

        val home = HomeFragment()
        val resultList = home.filter(articleDataList, "s")
        val expectList = mutableListOf<Articles>()

        val louis = Users(id = "003", name = "Louis")
        val sylvie = Users(id = "004", name = "Sylvie")

        val article3 = Articles(title = "Gourmandism", author = louis)
        val article4 = Articles(title = "Table Gamer", author = sylvie)
        expectList.add(article3)
        expectList.add(article4)

        Assert.assertEquals(expectList, resultList)
    }
}