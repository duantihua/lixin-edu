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

package net.openurp.lixin.edu.program.service

import org.openurp.edu.program.model.{MajorPlan, Program}
import org.openurp.edu.program.service.PlanChecker

import java.time.YearMonth

/** 检查AI课程数量
 */
class AICourseCountPlanChecker extends PlanChecker {
  var minCount = 2

  override def suitable(program: Program): Boolean = {
    Set("本科").contains(program.level.name) && program.grade.beginIn.isAfter(YearMonth.of(2025, 7))
  }

  override def check(plan: MajorPlan): Seq[String] = {
    var aiCount = 0
    for (g <- plan.groups.filter(!_.courseType.practical); pc <- g.planCourses) {
      if (pc.course.name.contains("AI")) {
        aiCount += 1
      }
    }
    if (aiCount < minCount) {
      List(s"AI课程数量为${aiCount}不足${minCount}门")
    } else {
      List.empty
    }
  }
}
