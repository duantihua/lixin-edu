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

package net.openurp.lixin.edu

import net.openurp.lixin.edu.course.service.*
import net.openurp.lixin.edu.program.service.*
import org.beangle.commons.cdi.BindModule
import org.openurp.edu.program.service.checkers.{CourseHourPlanChecker, MajorCourseDocChecker, OptionalCreditHourPlanChecker, TermsPlanChecker}
import org.openurp.edu.program.service.impl.DefaultProgramChecker

class DefaultModule extends BindModule {
  override protected def binding(): Unit = {
    println("LIXIN EDU EXTENDED MODULE ACTIVE...........")

    bind(classOf[DefaultNewCourseService]).primary()

    //暂时不用
    bind("PlanChecker.AICourseCount", classOf[AICourseCountPlanChecker])

    bind("PlanChecker.englishCourseCount", classOf[EnglishCourseCountPlanChecker])
    bind("PlanChecker.majorOptional", classOf[MajorOptionalPlanChecker])
    bind("PlanChecker.stage", classOf[StagePlanChecker])
    bind("PlanChecker.creditStat", classOf[CreditStatPlanChecker])

    bind("PlanChecker.optionalCreditHour", classOf[OptionalCreditHourPlanChecker])
    bind("PlanChecker.courseHour", classOf[CourseHourPlanChecker])
    bind("PlanChecker.terms", classOf[TermsPlanChecker])
    bind("DocChecker.majorCourse", classOf[MajorCourseDocChecker])

    bind("ProgramChecker.lixin", classOf[DefaultProgramChecker])
      .property("planCheckers",
        list(
          ref("PlanChecker.optionalCreditHour"),

          ref("PlanChecker.creditStat"),
          ref("PlanChecker.englishCourseCount"),
          ref("PlanChecker.majorOptional"),
          ref("PlanChecker.stage"),

          ref("PlanChecker.courseHour"),
          ref("PlanChecker.terms")
        )
      ).property("docCheckers",
        list(
          ref("DocChecker.majorCourse")
        )
      ).primary()

    bind("PlanExcelReader.lixin", classOf[LixinPlanExcelReader]).primary()
  }
}
