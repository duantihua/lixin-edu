/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.openurp.lixin.edu.course.service

import org.beangle.commons.lang.{Numbers, Strings}
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.edu.model.Course
import org.openurp.code.edu.model.CourseRank
import org.openurp.edu.course.flow.{NewCourseApply, NewCourseDepart}
import org.openurp.edu.course.service.NewCourseService

class DefaultNewCourseService extends NewCourseService {

  var entityDao: EntityDao = _

  override def check(apply: NewCourseApply): Seq[String] = {
    //本学院没有开过，但是其他学院如果开设了同名课程，则不允许申请
    val query1 = OqlBuilder.from(classOf[Course], "c")
    query1.where("c.project=:project", apply.project)
    query1.where("c.department = :depart", apply.department)
    query1.where("c.name=:name", apply.name)
    val ownCourses = entityDao.search(query1)
    if (ownCourses.nonEmpty) {
      Seq.empty
    } else {
      val query = OqlBuilder.from(classOf[Course], "c")
      query.where("c.project=:project", apply.project)
      query.where("c.department != :depart", apply.department)
      query.where("c.name=:name", apply.name)
      query.where("c.endOn is null")
      val otherCourses = entityDao.search(query)
      if (otherCourses.nonEmpty) {
        val h = otherCourses.head
        Seq(s"${h.department.name}已经开设了类似课程${h.code} ${h.name}")
      } else {
        Seq.empty
      }
    }
  }

  override def generate(apply: NewCourseApply): String = {
    val departCode = entityDao.findBy(classOf[NewCourseDepart], "depart", apply.department).headOption.map(_.code).getOrElse(apply.department.code)
    val creditCode =
      if apply.defaultCredits % 1 > 0.1 then
        apply.defaultCredits.toInt.toString + "H"
      else
        "0" + apply.defaultCredits.toInt.toString
    val rankCode = if apply.rank.get.id == CourseRank.Compulsory then "1" else "2"
    val categoryCode = apply.category.code

    val seqCode = getSeq(apply, 3)
    s"${departCode}${seqCode}${creditCode}${rankCode}${categoryCode}"
  }

  private def getSeq(apply: NewCourseApply, seqLength: Int): String = {
    val departCode = entityDao.findBy(classOf[NewCourseDepart], "depart", apply.department).headOption.map(_.code).getOrElse(apply.department.code)
    val codePattern = s"${departCode}" + ("_" * seqLength) + "%"
    val q = OqlBuilder.from[String](classOf[Course].getName, "c")
    q.where("c.code like :pattern and c.name=:name", codePattern, apply.name)
    q.where("c.project=:project", apply.project)
    apply.code foreach { code =>
      q.where("c.code != :thisCourseCode", code)
    }
    q.select("c.code")
    val exists = entityDao.search(q)
    if (exists.nonEmpty) {
      exists.head.substring(departCode.length, departCode.length + seqLength) //从第二位开始取，取三位
    } else {
      val q = OqlBuilder.from[String](classOf[Course].getName, "c")
      q.where("c.code like :pattern", codePattern)
      q.where("c.project=:project", apply.project)
      apply.code foreach { code =>
        q.where("c.code != :thisCourseCode", code)
      }
      q.select("c.code")
      val codes = entityDao.search(q)
      if (codes.nonEmpty) {
        val seq = codes.map(_.substring(departCode.length, departCode.length + seqLength)).filter(x => Numbers.isDigits(x)).map(_.toInt).distinct.sorted
        var start = 1
        val iter = seq.iterator
        var found = false
        while (iter.hasNext && !found) {
          val n = iter.next
          if (n - start <= 1) {
            start = n
          } else {
            found = true
          }
        }
        Strings.leftPad((start + 1).toString, seqLength, '0')
      } else {
        Strings.leftPad("1", seqLength, '0')
      }
    }
  }
}
