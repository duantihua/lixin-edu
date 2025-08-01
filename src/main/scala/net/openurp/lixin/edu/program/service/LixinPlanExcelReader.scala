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

import org.beangle.data.dao.EntityDao
import org.openurp.edu.program.model.{MajorPlan, Program}
import org.openurp.edu.program.service.PlanExcelReader

import java.io.InputStream

class LixinPlanExcelReader extends PlanExcelReader {

  var entityDao: EntityDao = _

  override def process(program: Program, is: InputStream): (MajorPlan, Iterable[String]) = {
    val parser = new LixinPlanExcelParser(is)
    parser.process()
    val excelPlan = parser.plan
    val messages = parser.messages
    val majorPlan = excelPlan.convert(program, entityDao, messages)
    (majorPlan, messages)
  }
}
