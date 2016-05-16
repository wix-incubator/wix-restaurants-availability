package com.wix.restaurants.availability

import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class DateTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val date = new Date(2020, 9, 1, 8, 5)
    val serializedDate = "2020-09-01 08:05"
  }

  "toString" should {
    "serialize to standard format" in new Ctx {
      date.toString must beEqualTo(serializedDate)
    }
  }

  "parse" should {
    "deserialize from standard format" in new Ctx {
      Date.parse(serializedDate) must beEqualTo(date)
    }
  }
}
