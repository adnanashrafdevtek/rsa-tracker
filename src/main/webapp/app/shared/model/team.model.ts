import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ITask } from 'app/shared/model/task.model';
import { IMessage } from 'app/shared/model/message.model';
import { IDocument } from 'app/shared/model/document.model';

export interface ITeam {
  id?: number;
  name?: string;
  listTitle?: string;
  active?: boolean;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  modifiedDate?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  teamLead?: IUser | null;
  teamMembers?: IUser[] | null;
  tasks?: ITask[] | null;
  messages?: IMessage[] | null;
  documents?: IDocument[] | null;
}

export const defaultValue: Readonly<ITeam> = {
  active: false,
};
