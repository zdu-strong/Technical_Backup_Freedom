import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class PaginationModel {

  @jsonMember(Number)
  pageNum!: number;

  @jsonMember(Number)
  pageSize!: number;

  @jsonMember(Number)
  totalRecord!: number;

  @jsonMember(Number)
  totalPage!: number;

}