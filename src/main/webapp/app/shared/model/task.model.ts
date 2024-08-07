import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ITeam } from 'app/shared/model/team.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { TaskStatus } from 'app/shared/model/enumerations/task-status.model';

export interface ITask {
  id?: number;
  name?: string;
  description?: string;
  priority?: keyof typeof Priority;
  status?: keyof typeof TaskStatus;
  dueDate?: dayjs.Dayjs;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  modifiedDate?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  assignedTo?: IUser | null;
  team?: ITeam | null;
}

export const defaultValue: Readonly<ITask> = {};
